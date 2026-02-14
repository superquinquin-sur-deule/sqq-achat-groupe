<script setup lang="ts">
import { useForm, useField } from 'vee-validate'
import { toTypedSchema } from '@vee-validate/zod'
import { z } from 'zod'
import Input from '@/components/ui/Input.vue'
import Button from '@/components/ui/Button.vue'

const emit = defineEmits<{
  submit: [info: { name: string; email: string; phone: string }]
  back: []
}>()

const schema = toTypedSchema(
  z.object({
    name: z.string().min(1, "Merci d'indiquer votre nom"),
    email: z
      .string()
      .min(1, "Merci d'indiquer votre email")
      .email("Merci d'indiquer un email valide"),
    phone: z
      .string()
      .min(1, "Merci d'indiquer votre téléphone")
      .regex(/^[\d\s+()-]{10,}$/, "Merci d'indiquer un numéro valide"),
  }),
)

const { handleSubmit } = useForm({ validationSchema: schema })

const { value: name, errorMessage: nameError, handleBlur: nameBlur } = useField<string>('name')
const { value: email, errorMessage: emailError, handleBlur: emailBlur } = useField<string>('email')
const { value: phone, errorMessage: phoneError, handleBlur: phoneBlur } = useField<string>('phone')

const onSubmit = handleSubmit((values) => {
  emit('submit', values)
})
</script>

<template>
  <form class="mx-auto flex w-full max-w-[480px] flex-col gap-4" @submit.prevent="onSubmit">
    <Input
      id="customer-name"
      v-model="name"
      label="Nom complet"
      type="text"
      placeholder="Marie Dupont"
      autocomplete="name"
      :error="nameError ?? ''"
      required
      @blur="nameBlur"
    />
    <Input
      id="customer-email"
      v-model="email"
      label="Adresse email"
      type="email"
      placeholder="marie@exemple.fr"
      autocomplete="email"
      :error="emailError ?? ''"
      required
      @blur="emailBlur"
    />
    <Input
      id="customer-phone"
      v-model="phone"
      label="Téléphone"
      type="tel"
      placeholder="06 12 34 56 78"
      autocomplete="tel"
      :error="phoneError ?? ''"
      required
      @blur="phoneBlur"
    />
    <div class="mt-4 flex flex-col gap-2 sm:flex-row-reverse">
      <Button type="submit" variant="primary" class="w-full sm:w-auto sm:min-w-[200px]">
        Continuer
      </Button>
      <Button variant="secondary" class="w-full sm:w-auto" @click="emit('back')">
        Retour au panier
      </Button>
    </div>
  </form>
</template>
